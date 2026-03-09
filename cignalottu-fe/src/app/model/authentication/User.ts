import {RoleType} from './RoleType';
import {ProviderType} from './ProviderType';

export interface User{
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  role: RoleType;
  provider: ProviderType;
}
