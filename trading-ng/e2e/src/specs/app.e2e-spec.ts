import {HeaderPo} from '../po/header.po';
import 'jasmine';
import {browser, ExpectedConditions} from "protractor";
import {AppPo} from "../po/app.po";
import {LatestListPo} from "../po/latest-list.po";

describe('App', () => {

  it('should display default page',  async () => {
    await AppPo.navigateToMainPage();

    await browser.wait(ExpectedConditions.visibilityOf(HeaderPo.loginLink()));

    expect(await HeaderPo.loginLink().getText()).toEqual('LOG IN');
    expect(await LatestListPo.datagrid()).toBeTruthy();
  });
});
