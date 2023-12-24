import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { ConfirmationService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { ConfirmPopupModule } from 'primeng/confirmpopup';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { Pair } from '../model/pair';

@Component({
  selector: 'app-actions-pair',
  standalone: true,
  imports: [ReactiveFormsModule, TranslateModule, CommonModule, ButtonModule, DialogModule, InputTextModule, ConfirmPopupModule],
  providers: [ConfirmationService],
  templateUrl: './actions-pair.component.html',
  styleUrls: ['./actions-pair.component.scss']
})
export class ActionsPairComponent implements OnInit {

  @Input()
  pair: Pair;

  opened = false;

  @Output()
  removeConfirmed: EventEmitter<boolean> = new EventEmitter();

  @Output()
  updateConfirmed: EventEmitter<Pair> = new EventEmitter();

  pairForm: FormGroup;

  constructor(private confirmationService: ConfirmationService, private translateService: TranslateService) {}

  ngOnInit(): void {
    this.pairForm = new FormGroup({
      id: new FormControl(this.pair.id, Validators.required),
      name: new FormControl({value: this.pair.name, disabled: true}, Validators.required),
      symbol: new FormControl(this.pair.symbol, Validators.required),
      sourceCode: new FormControl({value: this.pair.sourceCode, disabled: true}, Validators.required),
      targetCode: new FormControl(this.pair.targetCode, Validators.required),
      exchange: new FormControl(this.pair.exchange, Validators.required),
      providerCode: new FormControl(this.pair.providerCode, Validators.required),
      lastUpdate: new FormControl(this.pair.lastUpdate, Validators.required),
    });
  }

  open() {
    this.opened = true;
  }

  update() {
    this.updateConfirmed.emit(this.pairForm.value);
    this.opened = false;
  }

  private delete(withSymbol: boolean) {
    this.removeConfirmed.emit(withSymbol);
    this.opened = false;
  }

  confirm(event: Event, withSymbol: boolean) {
    this.confirmationService.confirm({
      target: event.target as EventTarget,
      message: this.translateService.instant('admin.confirm-delete'),
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.delete(withSymbol);
      },
      reject: () => {
      }
    });
  }
}
