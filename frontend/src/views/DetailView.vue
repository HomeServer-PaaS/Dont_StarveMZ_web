<script setup>
import { onMounted, ref } from "vue";
import { RouterLink, useRoute } from "vue-router";
import { api, demoLogin, loadAuth } from "../services/api";

const route = useRoute();
const content = ref(null);
const authenticated = ref(false);
const loading = ref(true);
const failed = ref(false);
const favorited = ref(false);
const busy = ref(false);
const message = ref("");

async function refreshFavorite() {
  const { data } = await api.get("/me/favorites");
  favorited.value = data.some((item) => String(item.contentId) === String(content.value.contentId));
}

async function toggleFavorite() {
  if (busy.value || !content.value) return;
  busy.value = true;
  message.value = "";
  try {
    if (!authenticated.value) {
      await demoLogin();
      authenticated.value = true;
    }
    if (favorited.value) {
      await api.delete("/me/favorites/" + content.value.contentId);
      favorited.value = false;
      message.value = "찜 목록에서 뺐어요.";
    } else {
      await api.post("/me/favorites/" + content.value.contentId);
      favorited.value = true;
      message.value = "마이페이지에 저장했어요.";
    }
  } catch {
    message.value = "찜 정보를 저장하지 못했어요. 다시 시도해 주세요.";
  } finally {
    busy.value = false;
  }
}

onMounted(async () => {
  try {
    const auth = await loadAuth();
    authenticated.value = auth.authenticated;
    const result = await api.get("/contents/" + route.params.id);
    content.value = result.data;
    if (authenticated.value) await refreshFavorite();
  } catch {
    failed.value = true;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <section class="detail-page">
    <RouterLink class="back-link" to="/">← 혜택 목록으로</RouterLink>
    <div v-if="loading" class="state-panel">상세 정보를 불러오는 중이에요.</div>
    <div v-else-if="failed || !content" class="state-panel state-error">정보를 찾을 수 없어요.</div>
    <article v-else class="detail-card">
      <div class="detail-heading">
        <div class="card-topline">
          <span class="category-pill">{{ content.category }}</span>
          <span v-if="content.sourceCode === 'LOCAL_DEMO'" class="sample-pill">화면 예시</span>
        </div>
        <h1>{{ content.title }}</h1>
        <p class="detail-summary">{{ content.summary }}</p>
      </div>
      <div v-if="content.sourceCode === 'LOCAL_DEMO'" class="demo-warning">
        <strong>로컬 기능 확인용 예시</strong>
        <span>실제 정책, 자격 조건, 지원 금액을 안내하는 정보가 아닙니다.</span>
      </div>
      <div class="detail-body">{{ content.body }}</div>
      <div class="detail-actions">
        <a v-if="content.sourceCode !== 'LOCAL_DEMO' && content.sourceUrl" class="button button-quiet" :href="content.sourceUrl" target="_blank" rel="noreferrer">원문 확인 ↗</a>
        <button class="button button-dark" type="button" :disabled="busy" @click="toggleFavorite">
          {{ busy ? "저장 중..." : favorited ? "♥ 찜 해제" : "♡ 찜하기" }}
        </button>
      </div>
      <p v-if="message" class="action-message" role="status">{{ message }}</p>
    </article>
  </section>
</template>
