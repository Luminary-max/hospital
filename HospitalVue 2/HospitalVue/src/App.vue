<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
import { getToken, clearToken } from "@/utils/storage.js";

const INACTIVITY_TIMEOUT = 30 * 60 * 1000; // 30分钟无操作自动登出

export default {
  name: "App",
  data() {
    return { timer: null };
  },
  methods: {
    resetTimer() {
      if (this.timer) clearTimeout(this.timer);
      if (!getToken()) return;
      this.timer = setTimeout(() => {
        clearToken();
        this.$alert("长时间未操作，已自动安全登出", "安全提示", {
          confirmButtonText: "重新登录",
          type: "warning"
        }).then(() => {
          window.location.href = "/login";
        });
      }, INACTIVITY_TIMEOUT);
    }
  },
  mounted() {
    const events = ["mousedown", "mousemove", "keydown", "scroll", "touchstart"];
    events.forEach(e => window.addEventListener(e, this.resetTimer));
    this.resetTimer();
  },
  beforeDestroy() {
    if (this.timer) clearTimeout(this.timer);
  }
};
</script>

<style lang="scss">
#app{
  height: 100%;
}
</style>
