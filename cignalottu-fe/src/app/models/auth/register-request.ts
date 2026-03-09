import {RoleType} from './role-type';

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  role?: RoleType;
}
