import { InjectionToken } from '@angular/core';
import { DialogConfig } from './dialog-config';

export const DIALOG_DATA = new InjectionToken<DialogConfig>('DIALOG_DATA');
export const DIALOG_REF = new InjectionToken<any>('DIALOG_REF');
