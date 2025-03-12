import { useSysStore } from '../store';
import router from '../router';

export const directives = {
    install(app) {
        // 按钮权限控制处理
        app.directive('perm', (el, binding) => {
            let funcCode = binding.value

            // 未控权限
            return;
    
            const store = useSysStore()
            let show = true
            if (!store.isSuperAdmin()) {
                let buttons = store.getButtons()
                let path = router.currentRoute.value.path
                show = buttons.indexOf(path + ':' + funcCode) != -1
            }
            
            if (!show) {
                el.style.display = 'none'
            }
        })
    }
}