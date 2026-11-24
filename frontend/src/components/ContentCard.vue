<script setup>
import { RouterLink } from "vue-router";

defineProps({
  content: { type: Object, required: true }
});
</script>

<template>
  <RouterLink class="content-card" :to="{ name: 'detail', params: { id: content.contentId } }">
    <div class="card-topline">
      <span class="category-pill">{{ content.category }}</span>
      <span v-if="content.sourceCode === 'LOCAL_DEMO'" class="sample-pill">화면 예시</span>
      <span v-else class="card-arrow" aria-hidden="true">↗</span>
    </div>
    <h2>{{ content.title }}</h2>
    <p>{{ content.summary }}</p>
    <div class="card-bottom">
      <span>자세히 보기</span>
      <span class="arrow-circle">→</span>
    </div>
  </RouterLink>
</template>
