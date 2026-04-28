import { Component, ComponentRef, Inject, inject, Optional } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComponentPortal, PortalModule } from '@angular/cdk/portal';
import { DialogConfig } from '../../utils/dialog/dialog-config';
import { DialogRef } from '../../utils/dialog/dialog-ref';
import { DIALOG_DATA } from '../../utils/dialog/dialog-tokens';
import { LucideAngularModule, LucideIconData, Mail, User, XIcon } from 'lucide-angular';

@Component({
  selector: 'app-dialog',
  imports: [CommonModule, PortalModule, LucideAngularModule],
  templateUrl: './app-dialog.html',
  styleUrl: './app-dialog.css',
})
export class AppDialog {
  protected readonly xIcon = XIcon;
  protected readonly mailIcon = Mail;
  config = inject(DIALOG_DATA);
  private dialogRef = inject(DialogRef);

  portal = new ComponentPortal(this.config.component);

  close() {
    this.dialogRef.close();
  }
}
