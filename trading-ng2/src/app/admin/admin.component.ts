import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PortofolioService } from '../portofolio';
import { AdminService } from './admin.service';
import { Pair } from './model/pair';
import { TranslateService } from '@ngx-translate/core';

class Result {

  static create(pair: Pair): Result {
      return new Result(pair.symbol, pair.name, pair.exchange, null, pair.providerCode);
  }

  constructor(
    public code: string,
    public name: string,
    public exchange: string,
    public type: string,
    public provider: string) {}
}

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css']
})
export class AdminComponent implements OnInit {

  result: Result[];

  message: string;
  error: string;

  formGroup: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private adminService: AdminService,
    private portofolioService: PortofolioService,
    private translateService: TranslateService) { }

  ngOnInit() {

    this.formGroup = this.formBuilder.group({
      searchCode: ['']
    });

    this.updateTable();
  }

  updateTable() {
    this.adminService.getSymbols(this.formGroup.get('searchCode').value)
      .subscribe(result => {
        this.result = [...result.existingPairs.map(i => Result.create(i)), ...result.availableSymbols ];
      });
  }

  resetMessage() {
    this.message = null;
  }

  resetError(): void {
    this.error = null;
  }

  search() {
    this.resetError();
    this.updateTable();
  }

  add(code: string, provider: string) {
    this.resetError();

    this.adminService.addSymbol(code, provider)
    .subscribe(resp => {
      this.message = resp.message;
      this.updateTable();
      this.portofolioService.refreshAvailableSymbols();
    }, error => {
      this.error = this.translateService.instant(error.error.message, error.error.args);
    });
  }

  remove(symbol: string, provider: string) {
    this.resetError();

    this.adminService.removeSymbol(symbol, provider)
    .subscribe(resp => {
      this.message = resp.message;
      this.updateTable();
      this.portofolioService.refreshAvailableSymbols();
    });
  }
}
