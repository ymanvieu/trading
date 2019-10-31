import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared.module';
import { HighchartsChartModule } from 'highcharts-angular';
import { ChartRateComponent } from './chart-rate.component';

@NgModule({
  declarations: [
    ChartRateComponent
  ],
  imports: [
    SharedModule,
    HighchartsChartModule
  ],
  exports: [ChartRateComponent]
})
export class ChartRateModule {}
