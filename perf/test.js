import http from 'k6/http';
import { check, group } from "k6";
import { Rate } from "k6/metrics";

const myFailRate = new Rate('failed requests');

export default function (path) {
    let group_duration;
    let start = new Date();
    let retrieve_response = http.get(path + '/retrieve?vuNum=' + __VU);
    group_duration = new Date() - start;
    console.log("VU user - " + __VU + ". Retrieve users duration - " + group_duration + "ms");
    check(retrieve_response, {
        'Retrieve users from API : status 200': (r) => r.status === 200
    });
    myFailRate.add(retrieve_response.length > 0);
    console.log("VU user - " + __VU + ". Retrieve users length - " + retrieve_response.json().length);
    let save_requests = [];
    retrieve_response.json().forEach(function (item) {
        if (!item.error === true) {
            save_requests.push({
                'method': 'POST',
                'url': path + '/save',
                'body': JSON.stringify(item),
                'params': {headers: {'Content-Type': 'application/json'}}
            });
        }
    })
    console.log("VU user - " + __VU + ". Save request length - " + save_requests.length);

    group('Save', function () {
        start = new Date();
        let responses = http.batch(save_requests);
        responses.forEach(response => {
                check(response, {
                    "Save rq : status 200": (r) => r.status === 200,
                    'Save rq : response is not empty': (r) => r.json()["id"] !== null
                });
                myFailRate.add(response.status !== 200);
            }
        );
        group_duration = new Date() - start;
        console.log("VU user - " + __VU + ". Save group duration - " + group_duration + "ms")
    });
    start = new Date();
    let get_all_response = http.get(path + '/users?vuNum=' + __VU);
    group_duration = new Date() - start;
    console.log("VU user - " + __VU + ". Get users from db duration - " + group_duration + "ms");
    check(retrieve_response, {
        'Get all users from API : status 200': (r) => r.status === 200
    });
    myFailRate.add(get_all_response.length === save_requests.length);

    let delete_requests = [];
    get_all_response.json().forEach(function (item) {
        delete_requests.push({
            'method': 'DELETE',
            'url': path + '/delete?id=' + item.id,
            'body': null
        });
    })
    group('Delete', function () {
        start = new Date();
        let responses = http.batch(delete_requests);
        responses.forEach(response => {
                check(response, {
                    "Delete rq : status 200": (r) => r.status === 204
                });
                myFailRate.add(response.status !== 204);
            }
        );
        group_duration = new Date() - start;
        console.log("VU user - " + __VU + ". Delete group duration - " + group_duration + "ms")
    });
}