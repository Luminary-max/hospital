import axios from "axios"
import {getToken, clearToken} from "@/utils/storage.js";

const request = axios.create({
  baseURL: "",
  timeout: 8000
});

request.interceptors.request.use(config => {
  const token = getToken();
  if(token !== null){
      config.headers["token"] = token;
  }
  return config;
  },error => {
  return Promise.reject(error);
});

// 全局响应拦截 - token 过期自动跳转登录
request.interceptors.response.use(
  response => {
    if (response.data && response.data.state === false && response.data.msg && response.data.msg.indexOf("token") >= 0) {
      clearToken();
      window.location.href = "/login";
    }
    return response;
  },
  error => {
    return Promise.reject(error);
  }
);

export default request;