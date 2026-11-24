<script setup>
import { onMounted } from "vue";
import AppHeader from "./components/AppHeader.vue";
import { ensureCsrf } from "./services/api";

onMounted(() => {
  ensureCsrf().catch(() => {});
});
</script>

<template>
  <div class="app-shell">
    <AppHeader />
    <main class="page-shell">
      <RouterView />
    </main>
    <footer class="site-footer">
      <span>굶지마MZ</span>
      <span>작은 혜택을 모아, 생활의 여유를 더해요.</span>
    </footer>
  </div>
</template>
