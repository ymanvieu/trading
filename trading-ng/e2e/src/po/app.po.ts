import {browser} from "protractor";

export class AppPo {

    static navigateToMainPage() {
        return browser.get('/');
    }
}
