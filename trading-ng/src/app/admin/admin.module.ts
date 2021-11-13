import { NgModule } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from 'app/shared.module';
import { AdminComponent } from './admin.component';

@NgModule({
  imports: [
    ReactiveFormsModule,
    SharedModule,
  ],
  declarations: [AdminComponent],
  exports: [AdminComponent]
})
export class AdminModule { }
