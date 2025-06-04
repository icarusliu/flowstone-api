<template>
    <!-- 折线图 -->
    <div ref="chartRef" class="w-100 h-100"></div>
</template>
<script setup>
import { onMounted, reactive, ref } from "vue";
import * as echarts from "echarts";

const { xField, yFields, colors } = defineProps({
    xField: { type: String },
    yFields: { type: Array },
    colors: { type: Array },
});
const chartRef = ref();
const options = reactive({
    tooltip: {
        trigger: "axis",
    },
    legend: {
        data: [],
    },
    grid: {
        left: "20px",
        right: "30px",
        bottom: "20px",
        containLabel: true,
    },
    xAxis: {
        type: "category",
        boundaryGap: true,
        data: [],
    },
    yAxis: {
        type: "value",
    },
    series: [],
});
let chart;
let observer = new ResizeObserver(() => {
    chart && chart.resize();
});

onMounted(() => {
    chart = echarts.init(chartRef.value, "light");
    observer.observe(chartRef.value);
});

function reload(data) {
    if (!data) {
        return;
    }

    let xarr = [];
    let yarr = [];
    let legend = [];
    data.forEach((item) => {
        xarr.push(item[xField]);
    });

    let allLine = true;
    yFields.forEach((yField) => {
        let arr = [];
        data.forEach((item) => {
            arr.push(item[yField.prop]);
        });

        yarr.push({
            ...yField,
            data: arr,
        });

        if (yField.type == "bar") {
            allLine = false;
        }
        legend.push(yField.name);
    });

    if (allLine) {
        options.xAxis.boundaryGap = false;
    }

    options.xAxis.data = xarr;
    options.series = yarr;
    options.color = colors;
    options.legend.data = legend;

    console.log(options);

    chart.setOption(options);
}

defineExpose({
    reload,
});
</script>

<style lang="scss" scoped></style>
