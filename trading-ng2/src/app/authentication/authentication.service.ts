import { throwError as observableThrowError, Observable, BehaviorSubject } from 'rxjs';
import { catchError, tap, map, switchMap, filter, first } from 'rxjs/operators';
import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { AuthService } from 'ngx-auth';
import { TokenStorage } from './token-storage.service';
import { NgxPermissionsService } from 'ngx-permissions';
import * as jwt_decode from 'jwt-decode';

interface AccessData {
  accessToken: string;
  refreshToken: string;
}

@Injectable({providedIn: 'root'})
export class AuthenticationService implements AuthService {

  private readonly REFRESH_URL = '/api/refresh';

  private user$: BehaviorSubject<string> = new BehaviorSubject(null);

  constructor(
    private http: HttpClient,
    private tokenStorage: TokenStorage,
    private permissionsService: NgxPermissionsService
  ) {}

  getUser(): Observable<string> {
    return this.user$;
  }

  private setUser(user: string) {
    this.user$.next(user);
  }

  /**
   * Check, if user already authorized.
   * @description Should return Observable with true or false values
   * @returns {Observable<boolean>}
   * @memberOf AuthService
   */
  public isAuthorized(): Observable<boolean> {
    return this.getAccessToken().pipe(map(token => !!token));
  }

  /**
   * Get access token
   * @description Should return access token in Observable from e.g.
   * localStorage
   * @returns {Observable<string>}
   */
  public getAccessToken(): Observable<string> {
    return this.tokenStorage.getAccessToken().pipe(first());
  }

  /**
   * Function, that should perform refresh token verifyTokenRequest
   * @description Should be successfully completed so interceptor
   * can execute pending requests or retry original one
   * @returns {Observable<any>}
   */
  public refreshToken(): Observable<AccessData> {
    return this.tokenStorage
      .getRefreshToken()
      .pipe(
        first(),
        switchMap((refreshToken: string) => {
          return this.http.post<AccessData>(this.REFRESH_URL, {refreshToken});
        }),
        tap(this.saveAccessData.bind(this)),
        tap(data => this.refreshUser()),
        catchError((err) => {
          this.logout();

          return observableThrowError(err);
        })
      );
  }

  /**
   * Function, checks response of failed request to determine,
   * whether token be refreshed or not.
   * @description Essentialy checks status
   * @param {Response} response
   * @returns {boolean}
   */
  public refreshShouldHappen(response: HttpErrorResponse): boolean {
    return response.status === 401 || response.status === 403;
  }

  refreshUser() {
    this.getAccessToken()
    .pipe(
      filter(token => !!token))
    .subscribe(token => {
      try {
        const tokenInfo = jwt_decode(token);
        this.setUser(tokenInfo.sub);
        this.permissionsService.loadPermissions([tokenInfo.roles]);
      } catch (error) {
        console.error(error);
        this.setUser(null);
        this.permissionsService.flushPermissions();
      }
    });
  }

  /**
   * Verify that outgoing request is refresh-token,
   * so interceptor won't intercept this request
   * @param {string} url
   * @returns {boolean}
   */
  public verifyTokenRequest(url: string): boolean {
    return url.endsWith(this.REFRESH_URL);
  }

  public login(login: string, password: string): Observable<AccessData> {
    return this.http.post<AccessData>('/api/auth', {username: login, password: password}).pipe(
      tap((tokens) => this.saveAccessData(tokens)),
      tap(() => this.refreshUser()));
  }

  signup(login: string, password: string, recaptchaResponse?: string): Observable<AccessData> {
    return this.http.post<AccessData>('/api/signup', {login: login, password: password, recaptchaResponse: recaptchaResponse}).pipe(
    tap((tokens) => this.saveAccessData(tokens)),
    tap(() => this.refreshUser()));
  }

  public logout(): void {
    this.tokenStorage.clear();
    this.setUser(null);
    this.permissionsService.flushPermissions();
  }

  /**
   * Save access data in the storage
   *
   * @private
   * @param {AccessData} data
   */
  private saveAccessData({accessToken, refreshToken}: AccessData) {
    this.tokenStorage
      .setAccessToken(accessToken)
      .setRefreshToken(refreshToken);
  }

}
