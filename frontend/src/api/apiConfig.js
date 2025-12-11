import axios from 'axios';
import { setupAxiosInspector } from './utils/axiosInspector';


const PREFIX = 'spms/'
const api = axios.create({ baseURL: PREFIX + 'api/v1'});
setupAxiosInspector(api);

export default function () {
    return api;
}


