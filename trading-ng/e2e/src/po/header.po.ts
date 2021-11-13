import {by, element} from 'protractor';

export class HeaderPo {

    static header() {
        return element(by.css('clr-header'));
    }

    static loginLink() {
        return HeaderPo.header().element(by.css('a[href="/login"]'));
    }
}
