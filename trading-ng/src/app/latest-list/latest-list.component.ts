import { RateService } from '../rate/rate.service';
import { Rate } from '../rate/model/rate';
import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from '../authentication';
import { SymbolService } from '../symbol/symbol.service';
import { ClrDatagridSortOrder, ClrDatagridComparatorInterface } from '@clr/angular';
import { map, tap, switchMap } from 'rxjs/operators';
import { RxStompService } from '@stomp/ng2-stompjs';
import { timer } from 'rxjs';
import { RxjsComponent } from 'app/shared/rxjs.component';

class FavoriteAndDateComparator implements ClrDatagridComparatorInterface<Rate> {
  compare(a: Rate, b: Rate) {
    if (a.favorite && !b.favorite) { return 1; }
    if (!a.favorite && b.favorite) { return -1; }
    return <any>a.date - <any>b.date;
  }
}

@Component({
  selector: 'app-latest-list',
  templateUrl: './latest-list.component.html',
  styleUrls: ['./latest-list.component.css']
})
export class LatestListComponent extends RxjsComponent implements OnInit {
  rates: Rate[];

  descSort = ClrDatagridSortOrder.DESC;
  sortFavoriteAndDate = new FavoriteAndDateComparator();

  isAuthorised: boolean;

  now: Date;

  constructor(
    private rateService: RateService,
    private authService: AuthenticationService,
    private symbolService: SymbolService,
    private rxStompService: RxStompService
  ) { super(); }

  ngOnInit(): void {

    timer(5000, 5000).subscribe(() => this.now = new Date());

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
