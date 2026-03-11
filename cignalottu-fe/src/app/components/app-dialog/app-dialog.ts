import {Component, Inject, inject, Optional} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ComponentPortal, PortalModule} from '@angular/cdk/portal';
import {DialogConfig} from '../../utils/dialog/dialog-config';
import {DialogRef} from '../../utils/dialog/dialog-ref';
import {DIALOG_DATA} from '../../utils/dialog/dialog-tokens';

@Component({
  selector: 'app-app-dialog',
    imports: [CommonModule, PortalModule],
  templateUrl: './app-dialog.html',
  styleUrl: './app-dialog.css',
})
export class AppDialog {

  portal: ComponentPortal<any>;
  dialogRef = inject(DialogRef);

  constructor(@Optional() @Inject(DIALOG_DATA) public config: DialogConfig
  ) {
   if (!config?.component) {
      throw new Error('Devi passare un componente da mostrare!');
    }

    this.portal = new ComponentPortal(config.component);
  }

  close(result?: any) {
    this.dialogRef.close(result);
  }
}
