import { Component, OnInit } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { NgxPermissionsService } from 'ngx-permissions';
import { MenuItem } from 'primeng/api';
import { distinctUntilChanged, tap, first, filter } from 'rxjs/operators';
import { AuthenticationService } from './authentication/authentication.service';
import { PortofolioService } from './portofolio/portofolio.service';
import { TokenStorage } from './authentication/token-storage.service';
import { Asset } from './portofolio/model/asset';
import { Router } from '@angular/router';
import { RxStompService } from './rx-stomp.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {

  baseCurrency: Asset;
  login: string;

  items: MenuItem[];

  logoutItems: MenuItem[];

  constructor(
    private authService: AuthenticationService,
    private portofolioService: PortofolioService,
    private translateService: TranslateService,
    private rxStompService: RxStompService,
    private tokenStorage: TokenStorage,
    private permissionsService: NgxPermissionsService,
    private router: Router
    ) {}

    private loadItems() {

      const isAdmin = this.permissionsService.getPermission('ROLE_ADMIN') !== undefined;
      let items: MenuItem[] = [];

      this.translateService.get(['app.name', 'logout.title']).subscribe(trads  => {
        items.push({label: trads['app.name'], routerLink: '/latest'});
        items.push({label: 'Portofolio', routerLink: '/portofolio'});

        if (isAdmin) {
          items.push({label: 'Admin', routerLink: '/admin'});
        }

        this.items = items;
        this.logoutItems = [{label: trads['logout.title'], command: () => this.logout()}];
      });
    }

  ngOnInit(): void {
    this.loadItems();

    this.portofolioService.getPortofolio().subscribe(p => this.baseCurrency = !!p ? p.baseCurrency : null);

    this.authService.getAccessToken()
        .pipe(
            first(),
            filter(token => !!token),
            tap(() => this.authService.refreshUser()))
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
        this.loadItems();
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
