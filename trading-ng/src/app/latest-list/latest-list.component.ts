import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';
import { SortEvent } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { Table, TableModule } from 'primeng/table';
import { AuthenticationService } from '../authentication/authentication.service';
import { RateService } from '../rate/rate.service';
import { Rate } from '../rate/model/rate';
import {Component, NgZone, OnInit} from '@angular/core';
import { RxStompService } from '../rx-stomp.service';
import { TimeAgoPipe } from '../shared/pipes/time-ago.pipe';
import { SymbolService } from '../symbol/symbol.service';
import { map, tap, switchMap } from 'rxjs/operators';
import { timer } from 'rxjs';
import { RxjsComponent } from 'app/shared/rxjs.component';

@Component({
  selector: 'app-latest-list',
  standalone: true,
  imports: [
    TranslateModule,
    TableModule,
    ButtonModule,
    InputTextModule,
    FormsModule,
    TimeAgoPipe,
    CommonModule,
    RouterModule
  ],
  templateUrl: './latest-list.component.html',
  styleUrls: ['./latest-list.component.scss']
})
export class LatestListComponent extends RxjsComponent implements OnInit {

  rates: Rate[];

  isAuthorised: boolean;

  now: Date;

  filter: string;

  constructor(
    private rateService: RateService,
    private authService: AuthenticationService,
    private symbolService: SymbolService,
    private rxStompService: RxStompService,
    private ngZone: NgZone
  ) { super(); }


  customSort(event: SortEvent) {
    event.data.sort((data1, data2) => {
      let value1 = data1[event.field];
      let value2 = data2[event.field];
      let result;

      if (value1 == null && value2 != null) result = -1;
      else if (value1 != null && value2 == null) result = 1;
      else if (value1 == null && value2 == null) result = 0;
      else if (typeof value1 === 'string' && typeof value2 === 'string') result = value1.localeCompare(value2);
      else if (event.field === 'favorite') result = this.compare(data1, data2);
      else result = value1 < value2 ? -1 : value1 > value2 ? 1 : 0;

      return event.order * result;
    });
  }

  compare(a: Rate, b: Rate) {
    if (a.favorite && !b.favorite) { return 1; }
    if (!a.favorite && b.favorite) { return -1; }
    return b.fromcur.name.localeCompare(a.fromcur.name);
  }

  clearTable(table: Table) {
    this.filter = null;
    table.clear();
  }

  ngOnInit(): void {

    this.ngZone.runOutsideAngular(() => {
      timer(5000, 5000)
          .subscribe(() => this.ngZone.run(() => this.now = new Date()));
    });

    this.register(
      this.rxStompService.watch('/topic/latest/')
      .pipe(map(msg => <Rate[]> JSON.parse(msg.body)))
      .subscribe(rates => {
        rates.forEach(ur => {
          const rate = this.rates.find(r => ur.fromcur.code === r.fromcur.code && ur.tocur.code === r.tocur.code);

          if (!!rate) {
            rate.value = ur.value;
            rate.date = ur.date;
          } else {
            this.rates.push(ur);
          }
        });
      })
    );

    this.register(
      this.authService.getUser()
        .pipe(
          map(login => !!login),
          tap(isAuthorised => this.isAuthorised = isAuthorised),
          switchMap(() => this.rateService.getLatest()))
        .subscribe(rates => this.rates = rates)
    );
  }

  switchFavorite(rate: Rate): void {
    if (rate.favorite) {
      this.symbolService.deleteFavoriteSymbol(rate.fromcur.code, rate.tocur.code).subscribe(() => rate.favorite = false);
    } else {
      this.symbolService.addFavoriteSymbol(rate.fromcur.code, rate.tocur.code).subscribe(() => rate.favorite = true);
    }
  }
}
