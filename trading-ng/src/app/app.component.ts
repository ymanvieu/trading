import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { RxStompService } from '@stomp/ng2-stompjs';
import { distinctUntilChanged, tap, first, filter, switchMap } from 'rxjs/operators';
import { AuthenticationService } from './authentication';
import { PortofolioService } from './portofolio';
import { TokenStorage } from './authentication/token-storage.service';
import { PromptUpdateService } from './prompt-update.service';
import { Asset } from './portofolio/model/asset';
import { Router } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  baseCurrency: Asset;
  login: string;

  constructor(
    private authService: AuthenticationService,
    private portofolioService: PortofolioService,
    translateService: TranslateService,
    private rxStompService: RxStompService,
    private tokenStorage: TokenStorage,
    promptUpdateService: PromptUpdateService,
    private router: Router
    ) {
      translateService.setDefaultLang('en');
    }

  ngOnInit(): void {
    this.portofolioService.getPortofolio().subscribe(p => this.baseCurrency = !!p ? p.baseCurrency : null);

    this.authService.getAccessToken()
        .pipe(
            first(),
            filter(token => !!token),
            switchMap(() => this.authService.refreshToken()))
        .subscribe();

    this.authService.getUser()
      .pipe(
        tap(login => this.login = login),
        tap(login => console.log('login: ', login)),
        distinctUntilChanged())
      .subscribe(login => {
        if (!!login) {
          this.portofolioService.refreshPortofolioAndAvailableSymbols();
        } else {
          this.portofolioService.clear();
        }
      });

    this.tokenStorage.getAccessToken()
      .subscribe(token => {
        if (!!token) {
          const headers = {};
          headers['Authorization'] = token;

          this.rxStompService.configure({ connectHeaders: headers });
        } else {
          this.rxStompService.configure({ connectHeaders: null });
        }

        this.rxStompService.stompClient.forceDisconnect();
      });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
