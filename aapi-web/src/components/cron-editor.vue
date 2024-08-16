<!-- cron表达式编辑 -->
<template>
    <el-input v-model="model" @change="onChange">
        <template #append>
            <el-popover width="400px" @hide="popoverHided" trigger="click" :visible="visible">
                <el-form v-model="config" label-position="top">
                    <el-form-item label="执行类型">
                        <el-radio-group v-model="config.type" @change="typeChanged">
                            <el-radio-button value="everyDay">每天一次</el-radio-button>
                            <el-radio-button value="hours">每天多次</el-radio-button>
                            <el-radio-button value="everyHour">每小时</el-radio-button>
                        </el-radio-group>
                    </el-form-item>

                    <el-form-item :label="timesLabel">
                        <el-time-select v-if="config.type == 'everyDay'" v-model="config.times" :teleported="false"
                            @change="timeChanged" start="00:00" end="11:55" step="00:15"></el-time-select>

                        <el-select v-else-if="config.type == 'everyHour'" v-model="config.times" :teleported="false"
                            multiple @change="timeChanged">
                            <el-option v-for="minute in minutes" :label="minute" :value="minute"></el-option>
                        </el-select>

                        <el-select v-else v-model="config.times" :teleported="false" multiple @change="timeChanged">
                            <el-option v-for="hour in hours" :value="hour">
                                {{ hour }}
                            </el-option>
                        </el-select>
                    </el-form-item>
                </el-form>

                <div class="text-center">
                    <el-button @click="visible = false" plain>关闭</el-button>
                </div>

                <template #reference>
                    <el-link type="primary" @click="visible = true">编辑</el-link>
                </template>
            </el-popover>
        </template>
    </el-input>
</template>

<script setup>
import * as _ from 'lodash'

const model = defineModel()
const emits = defineEmits(["change"])
const hours = ref([])
const config = ref({
    type: 'everyDay',
})
const visible = ref(false)

for (var i = 0; i <= 23; i++) {
    hours.value.push((i < 10 ? '0' + i : i) + '点')
}

const minutes = ref([])
for (var i = 0; i <= 59; i++) {
    minutes.value.push((i < 10 ? '0' + i : i) + '分')
}

const timesLabel = computed(() => {
    let type = config.value.type
    if (type == 'everyDay') {
        return '执行时间'
    } else if (type == 'everyHour') {
        return '执行分钟'
    } else {
        return '执行小时'
    }
})

function onChange(val) {
    emits('change', val)
}

function popoverHided() {
    config.value = {
        type: 'everyDay'
    }
}

function typeChanged() {
    config.value.times = null
}

function timeChanged() {
    let times = config.value.times
    if (!times) {
        return
    }

    let cron = ""
    let type = config.value.type
    if (type == 'everyDay') {
        // times格式是 HH:mm
        let arr = times.split(':')
        cron = `0 ${arr[1]} ${arr[0]} * * ?`
    } else if (type == 'everyHour') {
        // times格式是[mm]
        let timeStr = ""
        times.forEach(time => {
            timeStr += parseInt(time) + ','
        })

        timeStr = timeStr.substring(0, timeStr.length - 1)
        cron = `0 ${timeStr} * * * ?`
    } else {
        // times格式是[HH]
        let timeStr = ""
        times.forEach(time => {
            timeStr += parseInt(time) + ','
        })

        timeStr = timeStr.substring(0, timeStr.length - 1)
        cron = `0 0 ${timeStr} * * ?`
    }

    model.value = cron
}
</script>