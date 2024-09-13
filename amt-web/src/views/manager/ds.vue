<template>
    <div class="page-title">数据源</div>
    <entity-manager :fields="fields" apiPrefix="/base/ds" operationsWidth="100px"></entity-manager>
</template>

<script setup>
import { ref } from 'vue'
import { dsFields } from './fields';

const fields = ref(dsFields)

</script>