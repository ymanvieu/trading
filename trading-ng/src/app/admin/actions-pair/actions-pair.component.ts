import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClarityModule, ClrForm } from '@clr/angular';
import { TranslateModule } from '@ngx-translate/core';
import { Pair } from '../model/pair';

@Component({
  selector: 'app-actions-pair',
  templateUrl: './actions-pair.component.html',
  styleUrls: ['./actions-pair.component.scss'],
  standalone: true,
  imports: [ReactiveFormsModule, ClarityModule, TranslateModule, CommonModule]
})
export class ActionsPairComponent implements OnInit {

  @Input()
  pair: Pair;

  opened = false;

  @Output()
  removeConfirmed: EventEmitter<boolean> = new EventEmitter();

  @Output()
  updateConfirmed: EventEmitter<Pair> = new EventEmitter();

  @ViewChild(ClrForm, {static: true}) clrForm;

  pairForm: FormGroup;

  constructor() {}

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
  }

  delete(withSymbol: boolean) {
    this.removeConfirmed.emit(withSymbol);
  }
}
