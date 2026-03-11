import {EnvironmentInjector, inject, Injectable, Injector} from '@angular/core';
import {DialogConfig} from '../../utils/dialog/dialog-config';
import {AppDialog} from '../../components/app-dialog/app-dialog';
import {Overlay} from '@angular/cdk/overlay';
import {ComponentPortal} from '@angular/cdk/portal';
import {DialogRef} from '../../utils/dialog/dialog-ref';
import {DIALOG_DATA} from '../../utils/dialog/dialog-tokens';

@Injectable({
  providedIn: 'root',
})
export class DialogService {

  private overlay = inject(Overlay);
  private injector = inject(EnvironmentInjector);

  open<T>(config: DialogConfig<T>) {

    const overlayRef = this.overlay.create({
      hasBackdrop: true,
      positionStrategy: this.overlay
        .position()
        .global()
        .centerHorizontally()
        .centerVertically()
    });

    const dialogRef = new DialogRef(overlayRef);

    const containerPortal = new ComponentPortal(
      AppDialog,
      null,
      this.createInjector(config, dialogRef)
    );

    const containerRef = overlayRef.attach(containerPortal);

    containerRef.instance.portal = new ComponentPortal(
      config.component,
      null,
      this.createInjector(config, dialogRef)
    );

    if (!config.disableClose) {
      overlayRef.backdropClick().subscribe(() => dialogRef.close());
    }

    return dialogRef;
  }

  private createInjector(data: unknown, dialogRef: DialogRef) {
    return Injector.create({
      providers: [
        { provide: DIALOG_DATA, useValue: data },
        { provide: DialogRef, useValue: dialogRef }
      ],
      parent: this.injector
    });
  }


}
