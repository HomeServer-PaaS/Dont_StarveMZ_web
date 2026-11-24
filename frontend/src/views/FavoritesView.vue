<script setup>
import { onMounted, ref } from "vue";
import ContentCard from "../components/ContentCard.vue";
import { api, demoLogin, loadAuth } from "../services/api";

const contents = ref([]);
const authenticated = ref(false);
const loading = ref(true);
const failed = ref(false);
const signingIn = ref(false);

async function signIn() {
  signingIn.value = true;
  try {
    await demoLogin();
    authenticated.value = true;
    const { data } = await api.get("/me/favorites");
    contents.value = data;
  } catch {
    failed.value = true;
  } finally {
    signingIn.value = false;
  }
}

onMounted(async () => {
  try {
    const auth = await loadAuth();
    authenticated.value = auth.authenticated;
    if (authenticated.value) {
      const { data } = await api.get("/me/favorites");
      contents.value = data;
    }
  } catch {
    failed.value = true;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <section class="content-section favorites-page">
    <div class="section-heading">
      <div>
        <span class="eyebrow">MY PAGE</span>
        <h1>내가 찜한 정보</h1>
      </div>
      <span class="result-count">{{ contents.length }}개 저장</span>
    </div>
    <div v-if="loading" class="state-panel">찜 목록을 불러오는 중이에요.</div>
    <div v-else-if="failed" class="state-panel state-error">목록을 불러오지 못했어요. 다시 로그인해 주세요.</div>
    <div v-else-if="!authenticated" class="state-panel">
      <p>로그인하면 저장한 정보를 모아볼 수 있어요.</p>
      <button class="button button-dark" type="button" :disabled="signingIn" @click="signIn">
        {{ signingIn ? "로그인 중..." : "로컬 데모 로그인" }}
      </button>
    </div>
    <div v-else-if="contents.length === 0" class="state-panel">
      아직 찜한 정보가 없어요. <RouterLink to="/">혜택 둘러보기</RouterLink>
    </div>
    <div v-else class="content-grid">
      <ContentCard v-for="content in contents" :key="content.contentId" :content="content" />
    </div>
    <p class="demo-notice">로컬 데모 로그인으로 저장한 찜은 이 환경의 MySQL 데이터베이스에 보관됩니다.</p>
  </section>
</template>
