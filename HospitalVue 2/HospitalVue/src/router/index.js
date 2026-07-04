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
import ArrangeIndex from "@/views/ArrangeIndex.vue";
import SectionList from "@/views/SectionList.vue";
import AdminLayout from "@/views/AdminLayout.vue";
import DoctorLayout from "@/views/DoctorLayout.vue";
import PatientLayout from "@/views/PatientLayout.vue";
import SectionIndex from "@/views/SectionIndex.vue";
import ArrangeDoctor from "@/views/ArrangeDoctor.vue";
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

import AuditLogList from "@/views/AuditLogList.vue";

// 新增页面
import PharmacyDispensingList from "@/views/PharmacyDispensingList.vue";
import NotificationList from "@/views/NotificationList.vue";
import TriageRecordList from "@/views/TriageRecordList.vue";
import DrugBatchList from "@/views/DrugBatchList.vue";
import DrugCategoryList from "@/views/DrugCategoryList.vue";
import RechargeList from "@/views/RechargeList.vue";
import CheckResultList from "@/views/CheckResultList.vue";
import DoctorCheckOrder from "@/views/DoctorCheckOrder.vue";
import CashierSettlement from "@/views/CashierSettlement.vue";

// 患者端新增页面
import PatientDelivery from "@/views/PatientDelivery.vue";
import PatientReports from "@/views/PatientReports.vue";

// 新增页面
import DiagnosisDictList from "@/views/DiagnosisDictList.vue";
import EmrTemplateList from "@/views/EmrTemplateList.vue";
import PrescriptionTemplateList from "@/views/PrescriptionTemplateList.vue";
import RefundApproval from "@/views/RefundApproval.vue";
import InvoiceManage from "@/views/InvoiceManage.vue";
import DoctorStatsPanel from "@/views/DoctorStatsPanel.vue";
import InventoryCenter from "@/views/InventoryCenter.vue";
import PublicQueueDisplay from "@/views/PublicQueueDisplay.vue";

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
    path: "/publicQueue",
    component: PublicQueueDisplay
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
        path: "/pharmacyDispensingList",
        component: PharmacyDispensingList,
        meta: { requireAuth: true },
      },
      {
        path: "/notificationList",
        component: NotificationList,
        meta: { requireAuth: true },
      },
      {
        path: "/triageRecordList",
        component: TriageRecordList,
        meta: { requireAuth: true },
      },
      {
        path: "/drugBatchList",
        component: DrugBatchList,
        meta: { requireAuth: true },
      },
      {
        path: "/drugCategoryList",
        component: DrugCategoryList,
        meta: { requireAuth: true },
      },
      {
        path: "/inventoryCenter",
        component: InventoryCenter,
        meta: { requireAuth: true },
      },
      {
        path: "/auditLogList",
        component: AuditLogList,
        meta: { requireAuth: true },
      },
      {
        path: "/rechargeList",
        component: RechargeList,
        meta: { requireAuth: true },
      },
      {
        path: "/checkResultList",
        component: CheckResultList,
        meta: { requireAuth: true },
      },
      {
        path: "/cashierSettlement",
        component: CashierSettlement,
        meta: { requireAuth: true },
      },
      {
        path: "/diagnosisDictList",
        component: DiagnosisDictList,
        meta: { requireAuth: true },
      },
      {
        path: "/emrTemplateList",
        component: EmrTemplateList,
        meta: { requireAuth: true },
      },
      {
        path: "/prescriptionTemplateList",
        component: PrescriptionTemplateList,
        meta: { requireAuth: true },
      },
      {
        path: "/refundApproval",
        component: RefundApproval,
        meta: { requireAuth: true },
      },
      {
        path: "/invoiceManage",
        component: InvoiceManage,
        meta: { requireAuth: true },
      },
      {
        path: "/doctorStatsPanel",
        component: DoctorStatsPanel,
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
      {
        path: "/myNotificationList",
        component: NotificationList,
        meta: { requireAuth: true },
      },
      {
        path: "/patientDelivery",
        component: PatientDelivery,
        meta: { requireAuth: true },
      },
      {
        path: "/patientReports",
        component: PatientReports,
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
      {
        path: "/doctorCheckOrder",
        component: DoctorCheckOrder,
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
