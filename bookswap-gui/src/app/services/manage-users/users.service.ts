import { Injectable } from '@angular/core';
import {Observable} from "rxjs";
import {ProfileData} from "../../models/manage-users/ProfileData";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {UpdateUserData} from "../../models/manage-users/UpdateUserData";
import {ChangePassword} from "../../models/manage-users/ChangePassword";

const USERS_API = 'http://localhost:8080/users/';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  constructor(private http: HttpClient) { }

  getUserProfileData(): Observable<ProfileData>{
    return this.http.get<ProfileData>(USERS_API + 'user');
  }

  updateUserProfileData(updateUserData: UpdateUserData): Observable<any>{
    console.log("send" + updateUserData)
    return this.http.put(USERS_API + 'user', JSON.stringify(updateUserData), httpOptions);
  }

  changePassword(changePassword: ChangePassword): Observable<any>{
    return this.http.put(USERS_API + 'user/password', JSON.stringify(changePassword), httpOptions);
  }

}
