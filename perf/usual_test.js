import runTest from "./test.js";

const PATH = 'http://localhost:8082/usual';

export let options = {
    executor: 'per-vu-iterations',
    vus: 1,
    iterations: 1,
    thresholds: {
        'failed requests': ['rate<0.1']
    }
};

export default function () {
    runTest(PATH);
}