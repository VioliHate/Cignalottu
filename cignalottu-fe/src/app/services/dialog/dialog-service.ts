import { inject, Injectable, Injector } from '@angular/core';
import { DialogConfig } from '../../utils/dialog/dialog-config';
import { AppDialog } from '../../components/app-dialog/app-dialog';
import { Overlay } from '@angular/cdk/overlay';
import { ComponentPortal } from '@angular/cdk/portal';
import { DialogRef } from '../../utils/dialog/dialog-ref';
import { DIALOG_DATA, DIALOG_REF } from '../../utils/dialog/dialog-tokens';

@Injectable({
  providedIn: 'root',
})
export class DialogService {
  private overlay = inject(Overlay);
  private injector = inject(Injector);

  open<T>(config: DialogConfig<T>) {
    const overlayRef = this.overlay.create({
      hasBackdrop: true,
      backdropClass: 'bg-black/50',
      positionStrategy: this.overlay.position().global().centerHorizontally().centerVertically(),
      scrollStrategy: this.overlay.scrollStrategies.block(),
    });
    const dialogRef = new DialogRef(overlayRef);

    const customInjector = Injector.create({
      providers: [
        { provide: DIALOG_DATA, useValue: config },
        { provide: DialogRef, useValue: dialogRef },
        { provide: DIALOG_REF, useValue: dialogRef },
      ],
      parent: this.injector,
    });

    const portal = new ComponentPortal(AppDialog, null, customInjector);
    overlayRef.attach(portal);

    return dialogRef;
  }
}
