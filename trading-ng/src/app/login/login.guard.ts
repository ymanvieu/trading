import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {TokenStorage} from "../authentication/token-storage.service";
import {Observable} from "rxjs";
import {AuthenticationService} from "../authentication";

@Injectable()
export class LoginGuard implements CanActivate {

    constructor(
        private router: Router,
        private tokenStorage: TokenStorage,
        private auth: AuthenticationService) {
    }

    canActivate(
        next: ActivatedRouteSnapshot,
        state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        const token = next.queryParamMap.get('accessToken');
        const refreshToken = next.queryParamMap.get('refreshToken');

        if (!!token && !!refreshToken) {
            this.tokenStorage
                .setAccessToken(token)
                .setRefreshToken(refreshToken);
            this.auth.refreshUser();
            this.router.navigate([`/portofolio`]);
            return false;
        } else {
            return true;
        }
    }
}