import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import "@/assets/css/global.css";
import Login from "@/views/Login.vue";
import Admin from "@/views/Admin.vue";
import Doctor from "@/views/Doctor.vue";
import Patient from "@/views/Patient.vue";
import PatientList from "@/views/PatientList.vue";
import DoctorList from "@/views/DoctorList.vue";
import OrderList from "@/views/OrderList.vue";
import {getToken} from "@/utils/storage.js";
import OrderOperate from "@/views/OrderOperate.vue";
import SectionMessage from "@/views/SectionMessage.vue";
import MyOrder from "@/views/MyOrder.vue";
import OrderToday from "@/views/OrderToday.vue";
import DealOrder from "@/views/DealOrder.vue";
import DrugList from "@/views/DrugList.vue";
import CheckList from "@/views/CheckList.vue";
import DoctorOrder from "@/views/DoctorOrder.vue";
import InBed from "@/views/InBed.vue";
import ArrangeIndex from "@/views/ArrangeIndex.vue";
import SectionList from "@/views/SectionList.vue";
import AdminLayout from "@/views/AdminLayout.vue";
import DoctorLayout from "@/views/DoctorLayout.vue";
import PatientLayout from "@/views/PatientLayout.vue";
import SectionIndex from "@/views/SectionIndex.vue";
import ArrangeDoctor from "@/views/ArrangeDoctor.vue";
import MyBed from "@/views/MyBed.vue";
import BedList from "@/views/BedList.vue";
import DataExpore from "@/views/DataExpore.vue";
import echarts from 'echarts';//引入echarts
import DealOrderAgain from "@/views/DealOrderAgain.vue";
import DoctorCard from "@/views/DoctorCard.vue";
import PatientCard from "@/views/PatientCard.vue"

// 门诊新增页面
import QueueManage from "@/views/QueueManage.vue";
import DoctorQueue from "@/views/DoctorQueue.vue";
import PrescriptionList from "@/views/PrescriptionList.vue";
import QueueStatus from "@/views/QueueStatus.vue";
import MyPrescription from "@/views/MyPrescription.vue";
import MyEmr from "@/views/MyEmr.vue";
import ObserveBedList from "@/views/ObserveBedList.vue";

Vue.prototype.$echarts = echarts;//引入echarts
Vue.use(ElementUI);
Vue.use(VueRouter);

const routes = [
  {
    path: "*",
    redirect:"/login"
  },
  {
    path: "/login",
    component: Login
  },
  {
    path: "/admin",
    component: Admin,
    meta: {
      requireAuth: true,
    },
    children:[
      {
        path: "/adminLayout",
        component: AdminLayout,
        meta: { requireAuth: true },
      },
      {
        path: "/doctorList",
        component: DoctorList,
        meta: { requireAuth: true },
      },
      {
        path: "/patientList",
        component: PatientList,
        meta: { requireAuth: true },
      },
      {
        path: "/orderList",
        component: OrderList,
        meta: { requireAuth: true },
      },
      {
        path: "/drugList",
        component: DrugList,
        meta: { requireAuth: true },
      },
      {
        path: "/checkList",
        component: CheckList,
        meta: { requireAuth: true },
      },
      {
        path: "/bedList",
        component: BedList,
        meta: { requireAuth: true },
      },
      {
        path: "/dataExpore",
        component: DataExpore,
        meta: { requireAuth: true },
      },
      {
        path: "/arrangeIndex",
        component: ArrangeIndex,
        meta: { requireAuth: true },
        children:[
          {
            path: "/sectionIndex",
            component: SectionIndex,
            meta: { requireAuth: true },
          },
          {
            path: "/arrangeDoctor",
            component: ArrangeDoctor,
            meta: { requireAuth: true },
          },
        ]
      },
      {
        path: "/sectionList",
        component: SectionList,
        meta: { requireAuth: true },
      },
      {
        path: "/queueManage",
        component: QueueManage,
        meta: { requireAuth: true },
      },
      {
        path: "/observeBedList",
        component: ObserveBedList,
        meta: { requireAuth: true },
      },
    ]
  },
  {
    path: "/patient",
    component: Patient,
    meta: {
      requireAuth: true,
    },
    children:[
      {
        path: "/patientLayout",
        component: PatientLayout,
        meta: { requireAuth: true },
      },
      {
        path: "/orderOperate",
        component: OrderOperate
      },
      {
        path: "/sectionMessage",
        component: SectionMessage
      },
      {
        path: "/myOrder",
        component: MyOrder
      },
      {
        path: "/myBed",
        component: MyBed
      },
      {
        path: "/patientCard",
        component: PatientCard,
      },
      {
        path: "/queueStatus",
        component: QueueStatus,
        meta: { requireAuth: true },
      },
      {
        path: "/myPrescription",
        component: MyPrescription,
        meta: { requireAuth: true },
      },
      {
        path: "/myEmr",
        component: MyEmr,
        meta: { requireAuth: true },
      },
    ]
  },
  {
    path: "/doctor",
    component: Doctor,
    meta: {
      requireAuth: true,
    },
    children:[
      {
        path: "/doctorLayout",
        component: DoctorLayout,
        meta: { requireAuth: true },
      },
      {
        path: "/orderToday",
        component: OrderToday,
      },
      {
        path: "/dealOrder",
        component: DealOrder
      },
      {
        path: "/dealOrderAgain",
        component: DealOrderAgain
      },
      {
        path: "/doctorOrder",
        component: DoctorOrder,
      },
      {
        path: "/inBed",
        component: InBed,
      },
      {
        path: "/doctorCard",
        component: DoctorCard,
      },
      {
        path: "/doctorQueue",
        component: DoctorQueue,
        meta: { requireAuth: true },
      },
      {
        path: "/prescriptionList",
        component: PrescriptionList,
        meta: { requireAuth: true },
      },
    ],
  }
];

const router = new VueRouter({
  routes
});
//没登录的情况下，访问任何一个页面都会返回登录页面
router.beforeEach((to, from, next) => {
  if (to.meta.requireAuth) {
    const token = getToken();
    if (token !== null) {
      //直接放行
      next();
    } else {
      next("/login");
    }
  }
  else{
    next();
  }
});
export default router;
