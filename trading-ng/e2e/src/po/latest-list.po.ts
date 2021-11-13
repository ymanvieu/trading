import {by, element} from "protractor";

export class LatestListPo {

    static datagrid() {
        return element(by.css('clr-datagrid'));
    }
}