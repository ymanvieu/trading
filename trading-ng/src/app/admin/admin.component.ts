import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PortofolioService } from '../portofolio';
import { AdminService } from './admin.service';
import { Pair } from './model/pair';
import { TranslateService } from '@ngx-translate/core';
import { debounceTime, tap, switchMap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { SearchResult } from './model/search-result';

class Result {

  static create(pair: Pair): Result {
      return new Result(pair.symbol, pair.name, pair.exchange, null, pair.providerCode, pair.lastUpdate);
  }

  constructor(
    public code: string,
    public name: string,
    public exchange: string,
    public type: string,
    public providerCode: string,
    public lastUpdate?: Date) {}
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

    this.formGroup.get('searchCode').valueChanges
      .pipe(
        debounceTime(100),
        switchMap(code => this.updateSearchResult(code)))
      .subscribe(() => {
        this.resetError();
      });
  }

  private updateTable(): void {
    this.updateSearchResult(this.formGroup.get('searchCode').value).subscribe();
  }

  private updateSearchResult(searchCode: string): Observable<SearchResult> {
    return this.adminService.getSymbols(searchCode)
    .pipe(
      tap(result => {
        this.result = [...result.existingPairs.map(i => Result.create(i)), ...result.availableSymbols ];
      })
    );
  }

  resetMessage() {
    this.message = null;
  }

  resetError(): void {
    this.error = null;
  }

  add(symbol: string, providerCode: string) {
    this.resetError();

    this.adminService.addSymbol(symbol, providerCode)
    .subscribe(resp => {
      this.message = resp.message;
      this.updateTable();
      this.portofolioService.refreshAvailableSymbols();
    }, error => {
      this.error = this.translateService.instant(error.error.message, error.error.args);
    });
  }

  remove(symbol: string, providerCode: string) {
    this.resetError();

    this.adminService.removeSymbol(symbol, providerCode)
    .subscribe(resp => {
      this.message = resp.message;
      this.updateTable();
      this.portofolioService.refreshAvailableSymbols();
    }, error => {
      this.error = this.translateService.instant(error.error.message, error.error.args);
    });
  }
}
