import { tap } from 'rxjs/operators';
import { Order } from '../order/model/order';
import { OrderInfo } from '../order/model/order-info';
import { Portofolio } from './model/portofolio';
import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Symbol } from '../symbol/model/symbol';

@Injectable({providedIn: 'root'})
export class PortofolioService {

  private readonly url = '/api/portofolio';

  private portofolio$: BehaviorSubject<Portofolio> = new BehaviorSubject(null);
  private availableSymbols$: BehaviorSubject<Symbol[]> = new BehaviorSubject(null);

  constructor(
    private http: HttpClient
    ) { }

  clear(): void {
    this.availableSymbols$.next(null);
    this.portofolio$.next(null);
  }

  getPortofolio(): Observable<Portofolio> {
    return this.portofolio$;
  }

  private setPortofolio(portofolio: Portofolio) {
    this.portofolio$.next(portofolio);
  }

  getAvailableSymbols(): Observable<Symbol[]> {
    return this.availableSymbols$;
  }

  private setAvailableSymbols(symbols: Symbol[]) {
    this.availableSymbols$.next(symbols);
  }

  private getHttpPortofolio(): Observable<Portofolio> {
    return this.http.get<Portofolio>(`${this.url}`);
  }

  private getHttpAvailableSymbols(): Observable<Symbol[]> {
    return this.http.get<Symbol[]>(`${this.url}/available-symbols`);
  }

  getOrderInfo(selected: string, quantity: number): Observable<OrderInfo> {
    return this.http.get<OrderInfo>(`${this.url}/order-info`, {params: {selected: selected, quantity: String(quantity)}});
  }

  order(code: string, quantity: number, type: string): Observable<Order> {
    return this.http.post<Order>(`${this.url}/order`, {code: code, quantity: quantity, type: type})
    .pipe(tap(() => this.refreshPortofolioAndAvailableSymbols()));
  }

  refreshPortofolioAndAvailableSymbols() {
    combineLatest([this.getHttpPortofolio(), this.getHttpAvailableSymbols()])
    .subscribe(([p, s]) => {
      this.setPortofolio(p);
      this.setAvailableSymbols(s);
    }, () => {
      this.setPortofolio(null);
      this.setAvailableSymbols(null);
    });
  }

  refreshAvailableSymbols() {
    this.getHttpAvailableSymbols()
    .subscribe(symbols => {
      this.setAvailableSymbols(symbols);
    }, () => {
      this.setAvailableSymbols(null);
    });
  }

  refreshPortofolio() {
    this.getHttpPortofolio()
    .subscribe(p => {
      this.setPortofolio(p);
    }, () => {
      this.setPortofolio(null);
    });
  }
}
