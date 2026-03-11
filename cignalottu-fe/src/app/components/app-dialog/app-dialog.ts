import {Component, Optional} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ComponentPortal, PortalModule} from '@angular/cdk/portal';
import {DialogConfig} from '../../utils/dialog-config';
import {DialogRef} from '../../utils/dialog-ref';

@Component({
  selector: 'app-app-dialog',
    imports: [CommonModule, PortalModule],
  templateUrl: './app-dialog.html',
  styleUrl: './app-dialog.css',
})
export class AppDialog {

  portal: ComponentPortal<any>;

  constructor(
    @Optional() public config: DialogConfig,
    private dialogRef: DialogRef
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
