import { CommonModule, DatePipe } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { MessagesModule } from 'primeng/messages';
import { TableModule } from 'primeng/table';
import { Observable } from 'rxjs';
import { debounceTime, switchMap, tap } from 'rxjs/operators';
import { PortofolioService } from '../portofolio/portofolio.service';
import { ActionsPairComponent } from './actions-pair/actions-pair.component';
import { AdminService } from './admin.service';
import { Pair } from './model/pair';
import { SearchResult } from './model/search-result';
import { SearchResultToResultPipe } from './search-result-to-result.pipe';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [
    ReactiveFormsModule, CommonModule, TranslateModule, SearchResultToResultPipe, ActionsPairComponent,
    InputTextModule, TableModule, ButtonModule, MessagesModule
  ],
  providers: [DatePipe, MessageService],
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.scss'],
})
export class AdminComponent implements OnInit {

  result: SearchResult;

  searchFormGroup = new FormGroup({
    searchCode: new FormControl('')
  });

  constructor(
      private adminService: AdminService,
      private portofolioService: PortofolioService,
      private translateService: TranslateService,
      private datePipe: DatePipe,
      private messageService: MessageService) {
  }

  ngOnInit() {

    this.updateTable();

    this.searchFormGroup.get('searchCode').valueChanges
      .pipe(
        debounceTime(100),
        switchMap(code => this.updateSearchResult(code)))
      .subscribe(() => {
        this.resetMessages();
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

  resetMessages() {
    this.messageService.clear();
  }

  private addSuccessMessage(msg: string) {
    this.messageService.add({severity: 'success', detail: msg});
  }

  private addInfoMessage(msg: string) {
    this.messageService.add({severity: 'info', detail: msg});
  }

  private addErrorMessage(msg: string) {
    this.messageService.add({severity: 'error', detail: msg});
  }

  add(symbol: string, providerCode: string) {
    this.resetMessages();

    this.adminService.addPair(symbol, providerCode)
    .subscribe(pi => {
      const formattedDate = this.datePipe.transform(pi.quote.time, 'dd/MM/yy HH:mm:ss')
      this.addSuccessMessage(this.translateService.instant('admin.success.add', [pi.name, pi.code, pi.quote.price, pi.quote.currency, formattedDate]));
      this.updateTable();
      this.portofolioService.refreshAvailableSymbols();
    }, error => {
      this.addErrorMessage(this.translateService.instant(error.error.message, error.error.args));
    });
  }

  remove(pair: Pair, withSymbol: boolean) {
    this.resetMessages();

    this.addInfoMessage(this.translateService.instant('admin.info.delete', [pair.symbol]));

    this.adminService.removePair(pair.id, withSymbol)
    .subscribe(() => {
      this.addSuccessMessage(this.translateService.instant('admin.success.delete', [pair.symbol]));
      this.updateTable();
      this.portofolioService.refreshPortofolioAndAvailableSymbols()
    }, error => {
      this.addErrorMessage(this.translateService.instant(error.error.message, error.error.args));
    });
  }

  update(pair: Pair) {
    this.resetMessages();

    this.adminService.updatePair(pair)
        .subscribe(pi => {
          this.addSuccessMessage(this.translateService.instant('admin.success.update', [pi.name, pi.code]));
          this.updateTable();
          this.portofolioService.refreshPortofolioAndAvailableSymbols();
        }, error => {
          this.addErrorMessage(this.translateService.instant(error.error.message, error.error.args));
        });
  }
}
