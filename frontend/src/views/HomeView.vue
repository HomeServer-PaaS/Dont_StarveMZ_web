<script setup>
import { onMounted, ref } from "vue";
import ContentCard from "../components/ContentCard.vue";
import { api } from "../services/api";

const contents = ref([]);
const loading = ref(true);
const failed = ref(false);

onMounted(async () => {
  try {
    const { data } = await api.get("/contents");
    contents.value = data;
  } catch {
    failed.value = true;
  } finally {
    loading.value = false;
  }
});
</script>

<template>
  <section class="hero">
    <div class="hero-copy">
      <span class="eyebrow"><span class="eyebrow-dot"></span> 돈 되는 정보, 한눈에</span>
      <h1>놓치기 아까운 혜택,<br /><em>여기 모았어요.</em></h1>
      <p>청년 금융 정보와 생활에 보탬 되는 지원을<br class="desktop-break" /> 쉽고 편하게 둘러보세요.</p>
      <a class="hero-link" href="#benefits">혜택 둘러보기 <span>↓</span></a>
    </div>
    <div class="hero-art" aria-label="저축을 표현한 일러스트">
      <div class="sun-shape"></div>
      <div class="coin coin-one">₩</div>
      <div class="coin coin-two">₩</div>
      <div class="plant-pot"><span class="plant-stem"></span><span class="plant-leaf leaf-left"></span><span class="plant-leaf leaf-right"></span></div>
      <div class="art-caption">작은 절약이<br /><strong>큰 여유로</strong></div>
    </div>
  </section>

  <section id="benefits" class="content-section">
    <div class="section-heading">
      <div>
        <span class="eyebrow">TODAY'S PICKS</span>
        <h2>지금 살펴볼 정보</h2>
      </div>
      <span class="result-count">{{ contents.length }}개 정보</span>
    </div>

    <div v-if="loading" class="state-panel">정보를 불러오는 중이에요.</div>
    <div v-else-if="failed" class="state-panel state-error">
      정보를 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
    </div>
    <div v-else-if="contents.length === 0" class="state-panel">
      등록된 정보가 아직 없어요.
    </div>
    <div v-else class="content-grid">
      <ContentCard v-for="content in contents" :key="content.contentId" :content="content" />
    </div>
    <p class="demo-notice">화면에 보이는 ‘화면 예시’ 항목은 로컬 기능 확인용 샘플이며 실제 정책 정보가 아닙니다.</p>
  </section>
</template>
