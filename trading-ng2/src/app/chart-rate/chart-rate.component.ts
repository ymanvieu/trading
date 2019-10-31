import { map, first } from 'rxjs/operators';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RateService } from 'app/rate/rate.service';
import { Observable, Subscription, zip } from 'rxjs';
import * as Highcharts from 'highcharts';
import Stock from 'highcharts/modules/stock';
import { DatePipe, DecimalPipe } from '@angular/common';
import { Rate } from '../rate/model/rate';
import { RxStompService } from '@stomp/ng2-stompjs';
import { TranslateService } from '@ngx-translate/core';

Stock(Highcharts);

@Component({
  selector: 'app-chart-rate',
  templateUrl: './chart-rate.component.html',
  styleUrls: ['./chart-rate.component.css']
})
export class ChartRateComponent implements OnInit, OnDestroy {

  private topicSubscription: Subscription;

  private fromcur: string;
  private tocur: string;
  private latestRate: Rate;

  Highcharts = Highcharts;
  chartOptions: Highcharts.Options;
  updateFlag = false;
  private lastEvent: Highcharts.AxisSetExtremesEventObject;

  constructor(
    private rateService: RateService,
    private route: ActivatedRoute,
    private rxStompService: RxStompService,
    private translateService: TranslateService) {

    this.route.params.pipe(map(params => [params['fromcur'], params['tocur']]))
      .subscribe(([fromcur, tocur]) => {
        this.fromcur = fromcur;
        this.tocur = tocur;
      });
  }

  ngOnInit() {
    this.topicSubscription = this.rxStompService.watch(`/topic/latest/${this.fromcur}/${this.tocur}`)
    .pipe(map(msg => <Rate> JSON.parse(msg.body)))
    .subscribe((rate) => {
      if (!this.chartOptions) {
        return;
      }

      this.chartOptions.title.text = this.buildTitle(rate);
      this.updateFlag = true;
    });

    zip(
      this.getData(this.fromcur, this.tocur),
      this.rateService.getLatestFrom(this.fromcur, this.tocur)
    ).pipe(first())
    .subscribe(([data, latestRate]) => {
      this.latestRate = latestRate;
      this.initChart([...data, [latestRate.date, latestRate.value]]);
    });
  }

  ngOnDestroy() {
    this.topicSubscription.unsubscribe();
  }

  private initChart(data: any[]): void {
    this.chartOptions = {
      time: {
        useUTC: false
      },
      title: {
        text: this.buildTitle(this.latestRate)
      },
      xAxis: {
        ordinal: false,
        events: {
          setExtremes: this.setExtremes.bind(this)
        }
      },
      chart: {
        zoomType: 'x',
        backgroundColor: null
      },
      navigator: {
        adaptToUpdatedData: false
      },
      series: [{
        name: `${this.fromcur}/${this.tocur}`,
        type: 'line',
        data: data,
        dataGrouping: {
          enabled: false
        }
      }],
      scrollbar: {
        liveRedraw: false
      }
    };

    this.updateFlag = true;
  }

  private buildTitle(rate: Rate): string {
    const date = new DatePipe('en-US').transform(rate.date, 'yyyy-MM-dd HH:mm:ss');
    const value = new DecimalPipe('en-US').transform(rate.value);
    return this.translateService.instant('rate.latest', [rate.fromcur.code, rate.tocur.code, date, value]);
  }

  setExtremes(event: Highcharts.AxisSetExtremesEventObject): void {

    if (this.lastEvent === event) {
      console.log('same event, skip!');
      return;
    }

    console.log('event:', event);

    this.lastEvent = event;

    this.getData(this.fromcur, this.tocur, new Date(event.min), new Date(event.max))
      .subscribe(data => {
        this.chartOptions.series[0] = { type: 'line', data: data };
        this.updateFlag = true;
      });
  }

  getData(fromcur: string, tocur: string, startDate?: Date, endDate?: Date): Observable<any[]> {
    return this.rateService.getRaw(fromcur, tocur, startDate, endDate);
  }
}
