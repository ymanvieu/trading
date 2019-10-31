import { Observable, BehaviorSubject } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({providedIn: 'root'})
export class TokenStorage {

  private refreshToken$: BehaviorSubject<string> = new BehaviorSubject(null);
  private accessToken$: BehaviorSubject<string> = new BehaviorSubject(null);

  constructor(
  ) {
    const accesToken: string = <string>localStorage.getItem('accessToken');
    const refreshToken: string = <string>localStorage.getItem('refreshToken');

    this.accessToken$.next(accesToken);
    this.refreshToken$.next(refreshToken);
  }

  /**
   * Get access token
   * @returns {Observable<string>}
   */
  public getAccessToken(): Observable<string> {
    return this.accessToken$;
  }

  /**
   * Get refresh token
   * @returns {Observable<string>}
   */
  public getRefreshToken(): Observable<string> {
    return this.refreshToken$;
  }

  /**
   * Set access token
   * @returns {TokenStorage}
   */
  public setAccessToken(token: string): TokenStorage {
    localStorage.setItem('accessToken', token);
    this.accessToken$.next(token);
    return this;
  }

   /**
   * Set refresh token
   * @returns {TokenStorage}
   */
  public setRefreshToken(token: string): TokenStorage {
    localStorage.setItem('refreshToken', token);
    this.refreshToken$.next(token);
    return this;
  }

   /**
   * Remove tokens
   */
  public clear() {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    this.accessToken$.next(null);
    this.refreshToken$.next(null);
  }
}
