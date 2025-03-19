import cmpA from './a.jsx'

export default <cmpA a='test' onTest={(val) => alert(val)}>
    {{
        default: () => 'default slot',
        test: ({data}) => {
            return data
        }
    }}
</cmpA>