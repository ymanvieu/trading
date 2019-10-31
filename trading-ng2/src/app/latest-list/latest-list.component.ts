import { RateService } from '../rate/rate.service';
import { Rate } from '../rate/model/rate';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { AuthenticationService } from '../authentication';
import { SymbolService } from '../symbol/symbol.service';
import { ClrDatagridSortOrder, ClrDatagridComparatorInterface } from '@clr/angular';
import { map } from 'rxjs/operators';
import { RxStompService } from '@stomp/ng2-stompjs';
import { Subscription } from 'rxjs';

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
export class LatestListComponent implements OnInit, OnDestroy {
  rates: Rate[];

  descSort = ClrDatagridSortOrder.DESC;
  sortFavoriteAndDate = new FavoriteAndDateComparator();

  isAuthorised: boolean;

  private latestRatesTopicSubscription: Subscription;

  constructor(
    private rateService: RateService,
    private authService: AuthenticationService,
    private symbolService: SymbolService,
    private rxStompService: RxStompService
  ) {}

  ngOnInit(): void {

    this.latestRatesTopicSubscription = this.rxStompService.watch('/topic/latest/')
      .pipe(map(msg => <Rate[]> JSON.parse(msg.body)))
      .subscribe(rates => {
        rates.forEach(ur => {
          const rate = this.rates.find(r => ur.fromcur.code === r.fromcur.code && ur.tocur.code === r.tocur.code);

          if (!!rate) {
            rate.value = ur.value;
            rate.date = ur.date;
          }
        });
      });

    this.authService.getUser()
      .pipe(map(login => !!login))
      .subscribe(isAuthorised => this.isAuthorised = isAuthorised);

      this.rateService.getLatest().subscribe(rates => this.rates = rates);
  }

  ngOnDestroy() {
    this.latestRatesTopicSubscription.unsubscribe();
  }

  switchFavorite(rate: Rate): void {
    if (rate.favorite) {
      this.symbolService.deleteFavoriteSymbol(rate.fromcur.code, rate.tocur.code).subscribe(() => rate.favorite = false);
    } else {
      this.symbolService.addFavoriteSymbol(rate.fromcur.code, rate.tocur.code).subscribe(() => rate.favorite = true);
    }
  }
}
