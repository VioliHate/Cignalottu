import { Component, inject, Injector } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComponentPortal, PortalModule } from '@angular/cdk/portal';
import { DialogRef } from '../../utils/dialog/dialog-ref';
import { DIALOG_DATA } from '../../utils/dialog/dialog-tokens';
import { LucideAngularModule, Mail, XIcon } from 'lucide-angular';

@Component({
  selector: 'app-dialog',
  imports: [CommonModule, PortalModule, LucideAngularModule],
  templateUrl: './app-dialog.html',
  styleUrl: './app-dialog.css',
})
export class AppDialog {
  protected readonly xIcon = XIcon;
  protected readonly mailIcon = Mail;
  private injector = inject(Injector);

  config = inject(DIALOG_DATA);
  private dialogRef = inject(DialogRef);

  portal = new ComponentPortal(this.config.component);

  constructor() {
    this.portal = new ComponentPortal(this.config.component, null, this.injector);
  }

  close() {
    this.dialogRef.close();
  }
}
