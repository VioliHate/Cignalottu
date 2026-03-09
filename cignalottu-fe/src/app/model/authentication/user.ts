import {RoleType} from './role-type';
import {ProviderType} from './provider-type';

export interface User{
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: RoleType;
  provider: ProviderType;
}
