import {Type} from '@angular/core';

export interface DialogConfig<T = any> {
  component: Type<any>;
  data?: T;
  title?: string;
  width?: string;
  disableClose?: boolean;
}
