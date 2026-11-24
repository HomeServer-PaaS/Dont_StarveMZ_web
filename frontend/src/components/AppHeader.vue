<script setup>
import { onMounted, ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { demoLogin, loadAuth, logout } from "../services/api";

const router = useRouter();
const authenticated = ref(false);
const busy = ref(false);

async function refreshAuth() {
  try {
    const result = await loadAuth();
    authenticated.value = result.authenticated;
  } catch {
    authenticated.value = false;
  }
}

async function signIn() {
  if (busy.value) return;
  busy.value = true;
  try {
    await demoLogin();
    await refreshAuth();
  } finally {
    busy.value = false;
  }
}

async function signOut() {
  await logout();
  authenticated.value = false;
  await router.push("/");
}

async function openFavorites() {
  if (!authenticated.value) await signIn();
  if (authenticated.value) await router.push("/mypage/favorites");
}

onMounted(refreshAuth);
</script>

<template>
  <header class="topbar">
    <div class="topbar-inner">
      <RouterLink class="brand" to="/" aria-label="굶지마MZ 홈">
        <span class="brand-mark">굶</span>
        <span class="brand-copy">
          <strong>굶지마MZ</strong>
          <small>아끼는 생활의 시작</small>
        </span>
      </RouterLink>

      <nav class="main-nav" aria-label="주요 메뉴">
        <RouterLink to="/">혜택 둘러보기</RouterLink>
        <button class="nav-link" type="button" @click="openFavorites">내 찜</button>
      </nav>

      <div class="account-actions">
        <span v-if="authenticated" class="demo-label">LOCAL DEMO</span>
        <button v-if="authenticated" class="button button-quiet" type="button" @click="signOut">로그아웃</button>
        <button v-else class="button button-dark" type="button" :disabled="busy" @click="signIn">
          {{ busy ? "로그인 중..." : "로컬 데모 로그인" }}
        </button>
        <button v-if="authenticated" class="button button-accent" type="button" @click="openFavorites">마이페이지</button>
      </div>
    </div>
  </header>
</template>
