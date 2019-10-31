import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from 'app/shared.module';
import { AdminComponent } from './admin.component';
import { ClrSearchFieldModule } from '@porscheinformatik/clr-addons';

@NgModule({
  imports: [
    ReactiveFormsModule,
    SharedModule,
    ClrSearchFieldModule,
  ],
  declarations: [AdminComponent],
  exports: [AdminComponent]
})
export class AdminModule { }
