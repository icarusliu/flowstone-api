<template>
  <div class="d-flex h-100">
    <div class="left-nav h-100">
      <LeftNav class="left-menu" :type="type"></LeftNav>
    </div>

    <div class="right-panel flex-auto h-100">
      <TopNav />
      <div class="right-content b-box bg-white br-1 m-4 p-4 flex-auto">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup>
import LeftNav from '../components/left-menu.vue';
import TopNav from '../components/TopNav.vue';
import { useRouter } from 'vue-router';
import { computed } from 'vue';

const router = useRouter();
const type = computed(() => {
  return router.currentRoute.value.params.type;
});

</script>

<style scoped lang="scss">
.right-panel {
  overflow: hidden;
  background-color: #efefef;

  .right-content {
    position: relative;
    height: calc(100% - 80px);
    overflow-y: auto;
  }
}

</style>
