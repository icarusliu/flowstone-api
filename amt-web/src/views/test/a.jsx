export default (props, {emit, slots}) => {
    const test = () => emit('test', 12222)

    return <div onClick={test}>
        <span>{props.a}</span>
        <span>{slots.default()}</span>
        <span>{slots.test?.({data: 'testSlot'})}</span>
    </div>
}