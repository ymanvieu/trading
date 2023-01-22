import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ClarityModule } from '@clr/angular';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { debounceTime, switchMap, tap } from 'rxjs/operators';
import { PortofolioService } from '../portofolio';
import { ActionsPairComponent } from './actions-pair/actions-pair.component';
import { AdminService } from './admin.service';
import { Pair } from './model/pair';
import { SearchResult } from './model/search-result';
import { SearchResultToResultPipe } from './search-result-to-result.pipe';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
  standalone: true,
  imports: [
    ReactiveFormsModule,
    CommonModule,
    ClarityModule,
    TranslateModule,
    SearchResultToResultPipe,
    ActionsPairComponent
  ],
  providers: [
    DatePipe
  ],
})
export class AdminComponent implements OnInit {

  result: SearchResult;

  message: string;
  error: string;

  searchFormGroup = new FormGroup({
    searchCode: new FormControl('')
  });

  constructor(
    private adminService: AdminService,
    private portofolioService: PortofolioService,
    private translateService: TranslateService,
    private datePipe: DatePipe) { }

  ngOnInit() {

    this.updateTable();

    this.searchFormGroup.get('searchCode').valueChanges
      .pipe(
        debounceTime(100),
        switchMap(code => this.updateSearchResult(code)))
      .subscribe(() => {
        this.resetError();
      });
  }

  private updateTable(): void {
    this.updateSearchResult(this.searchCode).subscribe();
  }

  private updateSearchResult(searchCode: string): Observable<SearchResult> {
    return this.adminService.getSymbols(searchCode)
    .pipe(
      tap(result => {
        this.result = result;
      })
    );
  }

  getPairFromResult(id: number): Pair {
    return this.result.existingPairs.find(p => p.id === id);
  }

  get searchCode() {
    return this.searchFormGroup.value.searchCode;
  }

  resetSearchCode() {
    this.searchFormGroup.get('searchCode').setValue('');
  }

  resetMessage() {
    this.message = null;
  }

  resetError(): void {
    this.error = null;
  }

  add(symbol: string, providerCode: string) {
    this.resetError();

    this.adminService.addPair(symbol, providerCode)
    .subscribe(pi => {
      const formattedDate = this.datePipe.transform(pi.quote.time, 'dd/MM/yy HH:mm:ss')
      this.message = this.translateService.instant('admin.success.add', [pi.name, pi.code, pi.quote.price, pi.quote.currency, formattedDate]);
      this.updateTable();
      this.portofolioService.refreshAvailableSymbols();
    }, error => {
      this.error = this.translateService.instant(error.error.message, error.error.args);
    });
  }

  remove(pair: Pair, withSymbol: boolean) {
    this.resetError();

    this.adminService.removePair(pair.id, withSymbol)
    .subscribe(() => {
      this.message = this.translateService.instant('admin.success.delete', [pair.symbol]);
      this.updateTable();
      this.portofolioService.refreshPortofolioAndAvailableSymbols()
    }, error => {
      this.error = this.translateService.instant(error.error.message, error.error.args);
    });
  }

  update(pair: Pair) {
    this.resetError();

    this.adminService.updatePair(pair)
        .subscribe(pi => {
          this.message = this.translateService.instant('admin.success.update', [pi.name, pi.code]);
          this.updateTable();
          this.portofolioService.refreshPortofolioAndAvailableSymbols();
        }, error => {
          this.error = this.translateService.instant(error.error.message, error.error.args);
        });
  }
}
