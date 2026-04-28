import { Component, computed, inject, Injector } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ComponentPortal, PortalModule } from '@angular/cdk/portal';
import { DialogRef } from '../../utils/dialog/dialog-ref';
import { DIALOG_DATA } from '../../utils/dialog/dialog-tokens';
import { LucideAngularModule, Mail, XIcon } from 'lucide-angular';

@Component({
  selector: 'app-dialog',
  standalone: true,
  imports: [CommonModule, PortalModule, LucideAngularModule],
  templateUrl: './app-dialog.html',
  styleUrl: './app-dialog.css',
})
export class AppDialog {
  protected readonly xIcon = XIcon;
  protected readonly mailIcon = Mail;
  private readonly _injector = inject(Injector);
  readonly config = inject(DIALOG_DATA);
  private readonly _dialogRef = inject(DialogRef);

  portal = computed(() => {
    return new ComponentPortal(this.config.component, null, this._injector);
  });

  close() {
    this._dialogRef.close();
  }
}
