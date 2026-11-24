import { createRouter, createWebHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import DetailView from "../views/DetailView.vue";
import FavoritesView from "../views/FavoritesView.vue";

const router = createRouter({
  history: createWebHistory(),
  scrollBehavior() {
    return { top: 0 };
  },
  routes: [
    { path: "/", name: "home", component: HomeView },
    { path: "/contents/:id", name: "detail", component: DetailView },
    { path: "/mypage/favorites", name: "favorites", component: FavoritesView }
  ]
});

export default router;
