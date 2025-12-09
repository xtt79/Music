// 音乐后台管理系统主要JavaScript文件

// 全局变量
let currentUser = null;
let isLoggedIn = false;

// 模拟数据存储
let usersData = [];
let artistsData = [];
let albumsData = [];
let songsData = [];
let playlistsData = [];
let systemLogsData = [];

// 初始化应用
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

// 应用初始化
async function initializeApp() {
    try {
        // 加载模拟数据
        await loadMockData();
        
        // 检查登录状态
        checkLoginStatus();
        
        // 初始化页面功能
        initializePageFeatures();
        
        // 设置事件监听器
        setupEventListeners();
        
        console.log('音乐后台管理系统初始化完成');
    } catch (error) {
        console.error('初始化失败:', error);
    }
}

// 加载模拟数据
async function loadMockData() {
    try {
        // 由于使用本地JSON文件，我们直接赋值
        usersData = [
            {
                "id": "user001",
                "username": "admin",
                "email": "admin@musicplatform.com",
                "password": "admin123",
                "avatar": "resources/images/artist1.png",
                "role": "admin",
                "status": "active",
                "createdAt": "2024-01-15",
                "lastLogin": "2024-12-07"
            },
            {
                "id": "user002",
                "username": "musiclover2024",
                "email": "lover@music.com",
                "password": "pass123",
                "avatar": "resources/images/artist2.png",
                "role": "user",
                "status": "active",
                "createdAt": "2024-02-20",
                "lastLogin": "2024-12-06"
            }
        ];
        
        artistsData = [
            {
                "id": "artist001",
                "name": "Luna Star",
                "genre": "Pop",
                "avatar": "resources/images/artist1.png",
                "albumCount": 3,
                "songCount": 24,
                "status": "active",
                "debutYear": 2020,
                "description": "Rising pop sensation known for catchy melodies and powerful vocals"
            },
            {
                "id": "artist002",
                "name": "Thunder Rock",
                "genre": "Rock",
                "avatar": "resources/images/artist2.png",
                "albumCount": 5,
                "songCount": 42,
                "status": "active",
                "debutYear": 2015,
                "description": "Hard rock band with electrifying performances and meaningful lyrics"
            }
        ];
        
        albumsData = [
            {
                "id": "album001",
                "title": "Electric Dreams",
                "artist": "Luna Star",
                "artistId": "artist001",
                "cover": "resources/images/album1.png",
                "genre": "Pop",
                "releaseDate": "2024-03-15",
                "songCount": 8,
                "totalDuration": "32:45",
                "description": "A vibrant collection of pop anthems"
            },
            {
                "id": "album002",
                "title": "Thunder Strike",
                "artist": "Thunder Rock",
                "artistId": "artist002",
                "cover": "resources/images/album2.png",
                "genre": "Rock",
                "releaseDate": "2024-01-20",
                "songCount": 10,
                "totalDuration": "45:30",
                "description": "Powerful rock tracks with explosive energy"
            }
        ];
        
        songsData = [
            {
                "id": "song001",
                "title": "Electric Heartbeat",
                "artist": "Luna Star",
                "album": "Electric Dreams",
                "albumId": "album001",
                "duration": "3:45",
                "genre": "Pop",
                "fileUrl": "audio/song1.mp3",
                "coverUrl": "resources/images/album1.png",
                "playCount": 15420,
                "uploadDate": "2024-03-15",
                "lyrics": "Electric heartbeat in the night..."
            },
            {
                "id": "song002",
                "title": "Neon Lights",
                "artist": "Luna Star",
                "album": "Electric Dreams",
                "albumId": "album001",
                "duration": "4:12",
                "genre": "Pop",
                "fileUrl": "audio/song2.mp3",
                "coverUrl": "resources/images/album1.png",
                "playCount": 12890,
                "uploadDate": "2024-03-15",
                "lyrics": "Under the neon lights we dance..."
            }
        ];
        
        playlistsData = [
            {
                "id": "playlist001",
                "name": "Top Hits 2024",
                "description": "The hottest tracks of the year",
                "creator": "MusicAdmin",
                "creatorId": "user001",
                "coverUrl": "resources/images/album1.png",
                "songs": ["song001", "song002"],
                "isPublic": true,
                "createDate": "2024-01-01",
                "playCount": 25600,
                "likes": 1200
            }
        ];
        
        systemLogsData = [
            {
                "id": "log001",
                "timestamp": "2024-12-07T10:30:00Z",
                "user": "admin",
                "userId": "user001",
                "action": "LOGIN",
                "resource": "System",
                "details": "User logged in successfully",
                "ip": "192.168.1.100",
                "status": "SUCCESS"
            },
            {
                "id": "log002",
                "timestamp": "2024-12-07T10:35:00Z",
                "user": "admin",
                "userId": "user001",
                "action": "CREATE_ARTIST",
                "resource": "Artist",
                "details": "Created new artist: Luna Star",
                "ip": "192.168.1.100",
                "status": "SUCCESS"
            }
        ];
        
        console.log('模拟数据加载完成');
    } catch (error) {
        console.error('加载数据失败:', error);
    }
}

// 检查登录状态
function checkLoginStatus() {
    const loggedInUser = localStorage.getItem('currentUser');
    if (loggedInUser) {
        currentUser = JSON.parse(loggedInUser);
        isLoggedIn = true;
        updateUIForLoggedInUser();
    }
}

// 更新UI为登录状态
function updateUIForLoggedInUser() {
    if (currentUser) {
        const userAvatar = document.getElementById('userAvatar');
        const userName = document.getElementById('userName');
        
        if (userAvatar && currentUser.avatar) {
            userAvatar.src = currentUser.avatar;
        }
        if (userName) {
            userName.textContent = currentUser.username;
        }
    }
}

// 初始化页面功能
function initializePageFeatures() {
    const currentPage = getCurrentPage();
    
    switch (currentPage) {
        case 'index':
        case 'login':
            initializeLoginPage();
            break;
        case 'dashboard':
            initializeDashboard();
            break;
        case 'music-management':
            initializeMusicManagement();
            break;
        case 'user-management':
            initializeUserManagement();
            break;
        case 'playlist-management':
            initializePlaylistManagement();
            break;
        case 'system-logs':
            initializeSystemLogs();
            break;
    }
}

// 获取当前页面
function getCurrentPage() {
    const path = window.location.pathname;
    const page = path.split('/').pop().replace('.html', '');
    return page || 'index';
}

// 初始化登录页面
function initializeLoginPage() {
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
}

// 处理登录
function handleLogin(event) {
    event.preventDefault();
    
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
    const rememberMe = document.getElementById('rememberMe').checked;
    
    // 验证用户
    const user = usersData.find(u => u.username === username && u.password === password);
    
    if (user) {
        currentUser = user;
        isLoggedIn = true;
        
        // 保存登录状态
        if (rememberMe) {
            localStorage.setItem('currentUser', JSON.stringify(user));
        } else {
            sessionStorage.setItem('currentUser', JSON.stringify(user));
        }
        
        // 记录登录日志
        addSystemLog(user.username, user.id, 'LOGIN', 'System', 'User logged in successfully', 'SUCCESS');
        
        // 跳转到仪表盘
        window.location.href = 'dashboard.html';
    } else {
        showNotification('用户名或密码错误', 'error');
    }
}

// 处理退出
function handleLogout() {
    currentUser = null;
    isLoggedIn = false;
    localStorage.removeItem('currentUser');
    sessionStorage.removeItem('currentUser');
    
    // 记录退出日志
    addSystemLog('admin', 'user001', 'LOGOUT', 'System', 'User logged out', 'SUCCESS');
    
    window.location.href = 'index.html';
}

// 初始化仪表盘
function initializeDashboard() {
    if (!isLoggedIn) {
        window.location.href = 'index.html';
        return;
    }
    
    updateDashboardStats();
    initializeDashboardCharts();
    startRealTimeUpdates();
}

// 更新仪表盘统计
function updateDashboardStats() {
    const stats = {
        totalUsers: usersData.length,
        totalArtists: artistsData.length,
        totalAlbums: albumsData.length,
        totalSongs: songsData.length,
        totalPlaylists: playlistsData.length,
        totalPlays: songsData.reduce((sum, song) => sum + song.playCount, 0)
    };
    
    // 更新统计卡片
    updateStatCard('totalUsers', stats.totalUsers);
    updateStatCard('totalSongs', stats.totalSongs);
    updateStatCard('totalPlays', stats.totalPlays.toLocaleString());
    updateStatCard('totalArtists', stats.totalArtists);
}

// 更新统计卡片
function updateStatCard(elementId, value) {
    const element = document.getElementById(elementId);
    if (element) {
        element.textContent = value;
        
        // 添加动画效果
        element.parentElement.classList.add('animate-pulse');
        setTimeout(() => {
            element.parentElement.classList.remove('animate-pulse');
        }, 1000);
    }
}

// 初始化仪表盘图表
function initializeDashboardCharts() {
    // 用户增长趋势图
    initUserGrowthChart();
    
    // 音乐类型分布图
    initGenreDistributionChart();
    
    // 播放量统计图
    initPlayStatsChart();
}

// 初始化用户增长趋势图
function initUserGrowthChart() {
    const chartElement = document.getElementById('userGrowthChart');
    if (!chartElement) return;
    
    const chart = echarts.init(chartElement);
    
    const option = {
        title: {
            text: '用户增长趋势',
            textStyle: { color: '#ffffff' }
        },
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: ['1月', '2月', '3月', '4月', '5月', '6月'],
            axisLine: { lineStyle: { color: '#64748b' } },
            axisLabel: { color: '#94a3b8' }
        },
        yAxis: {
            type: 'value',
            axisLine: { lineStyle: { color: '#64748b' } },
            axisLabel: { color: '#94a3b8' }
        },
        series: [{
            data: [12, 25, 38, 45, 52, 61],
            type: 'line',
            smooth: true,
            lineStyle: { color: '#6366f1' },
            areaStyle: {
                color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                        { offset: 0, color: 'rgba(99, 102, 241, 0.3)' },
                        { offset: 1, color: 'rgba(99, 102, 241, 0.1)' }
                    ]
                }
            }
        }]
    };
    
    chart.setOption(option);
}

// 初始化音乐类型分布图
function initGenreDistributionChart() {
    const chartElement = document.getElementById('genreChart');
    if (!chartElement) return;
    
    const chart = echarts.init(chartElement);
    
    const option = {
        title: {
            text: '音乐类型分布',
            textStyle: { color: '#ffffff' }
        },
        tooltip: {
            trigger: 'item'
        },
        series: [{
            type: 'pie',
            radius: '70%',
            data: [
                { value: 35, name: 'Pop', itemStyle: { color: '#6366f1' } },
                { value: 28, name: 'Rock', itemStyle: { color: '#8b5cf6' } },
                { value: 20, name: 'Electronic', itemStyle: { color: '#06b6d4' } },
                { value: 12, name: 'Jazz', itemStyle: { color: '#10b981' } },
                { value: 5, name: 'Folk', itemStyle: { color: '#f59e0b' } }
            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }]
    };
    
    chart.setOption(option);
}

// 初始化播放量统计图
function initPlayStatsChart() {
    const chartElement = document.getElementById('playStatsChart');
    if (!chartElement) return;
    
    const chart = echarts.init(chartElement);
    
    const option = {
        title: {
            text: '每日播放量统计',
            textStyle: { color: '#ffffff' }
        },
        tooltip: {
            trigger: 'axis'
        },
        xAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
            axisLine: { lineStyle: { color: '#64748b' } },
            axisLabel: { color: '#94a3b8' }
        },
        yAxis: {
            type: 'value',
            axisLine: { lineStyle: { color: '#64748b' } },
            axisLabel: { color: '#94a3b8' }
        },
        series: [{
            data: [2340, 3200, 2800, 3500, 4200, 3800, 2900],
            type: 'bar',
            itemStyle: {
                color: {
                    type: 'linear',
                    x: 0, y: 0, x2: 0, y2: 1,
                    colorStops: [
                        { offset: 0, color: '#6366f1' },
                        { offset: 1, color: '#8b5cf6' }
                    ]
                }
            }
        }]
    };
    
    chart.setOption(option);
}

// 开始实时更新
function startRealTimeUpdates() {
    setInterval(() => {
        // 模拟实时数据更新
        const playCountElement = document.getElementById('totalPlays');
        if (playCountElement) {
            const currentValue = parseInt(playCountElement.textContent.replace(/,/g, ''));
            const newValue = currentValue + Math.floor(Math.random() * 10);
            updateStatCard('totalPlays', newValue.toLocaleString());
        }
    }, 5000);
}

// 初始化音乐管理
function initializeMusicManagement() {
    if (!isLoggedIn) {
        window.location.href = 'index.html';
        return;
    }
    
    loadArtistsTable();
    loadAlbumsTable();
    loadSongsTable();
    setupMusicManagementEvents();
}

// 加载歌手表格
function loadArtistsTable() {
    const tbody = document.getElementById('artistsTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    artistsData.forEach(artist => {
        const row = document.createElement('tr');
        row.className = 'hover:bg-slate-700 transition-colors';
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                    <div class="w-10 h-10 rounded-full overflow-hidden mr-3">
                        <img src="${artist.avatar}" alt="${artist.name}" class="w-full h-full object-cover">
                    </div>
                    <div class="text-sm font-medium text-white">${artist.name}</div>
                </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${artist.genre}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${artist.albumCount}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${artist.songCount}</td>
            <td class="px-6 py-4 whitespace-nowrap">
                <span class="px-2 py-1 text-xs font-semibold rounded-full ${
                    artist.status === 'active' ? 'bg-green-800 text-green-200' : 'bg-red-800 text-red-200'
                }">
                    ${artist.status === 'active' ? '活跃' : '停用'}
                </span>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${artist.debutYear}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button onclick="editArtist('${artist.id}')" class="text-indigo-400 hover:text-indigo-300 mr-3">编辑</button>
                <button onclick="deleteArtist('${artist.id}')" class="text-red-400 hover:text-red-300">删除</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// 加载专辑表格
function loadAlbumsTable() {
    const tbody = document.getElementById('albumsTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    albumsData.forEach(album => {
        const row = document.createElement('tr');
        row.className = 'hover:bg-slate-700 transition-colors';
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                    <div class="w-12 h-12 rounded overflow-hidden mr-3">
                        <img src="${album.cover}" alt="${album.title}" class="w-full h-full object-cover">
                    </div>
                    <div class="text-sm font-medium text-white">${album.title}</div>
                </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${album.artist}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${album.genre}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${album.songCount}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${album.releaseDate}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${album.totalDuration}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button onclick="editAlbum('${album.id}')" class="text-indigo-400 hover:text-indigo-300 mr-3">编辑</button>
                <button onclick="deleteAlbum('${album.id}')" class="text-red-400 hover:text-red-300">删除</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// 加载歌曲表格
function loadSongsTable() {
    const tbody = document.getElementById('songsTableBody');
    if (!tbody) return;
    
    tbody.innerHTML = '';
    
    songsData.forEach(song => {
        const row = document.createElement('tr');
        row.className = 'hover:bg-slate-700 transition-colors';
        row.innerHTML = `
            <td class="px-6 py-4 whitespace-nowrap">
                <div class="flex items-center">
                    <div class="w-10 h-10 rounded overflow-hidden mr-3">
                        <img src="${song.coverUrl}" alt="${song.title}" class="w-full h-full object-cover">
                    </div>
                    <div>
                        <div class="text-sm font-medium text-white">${song.title}</div>
                        <div class="text-sm text-slate-400">${song.artist}</div>
                    </div>
                </div>
            </td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${song.album}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${song.duration}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${song.genre}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${song.playCount.toLocaleString()}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm text-slate-300">${song.uploadDate}</td>
            <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                <button onclick="playSong('${song.id}')" class="text-green-400 hover:text-green-300 mr-3">播放</button>
                <button onclick="editSong('${song.id}')" class="text-indigo-400 hover:text-indigo-300 mr-3">编辑</button>
                <button onclick="deleteSong('${song.id}')" class="text-red-400 hover:text-red-300">删除</button>
            </td>
        `;
        tbody.appendChild(row);
    });
}

// 设置音乐管理事件
function setupMusicManagementEvents() {
    // 添加歌手按钮
    const addArtistBtn = document.getElementById('addArtistBtn');
    if (addArtistBtn) {
        addArtistBtn.addEventListener('click', showAddArtistModal);
    }
    
    // 添加专辑按钮
    const addAlbumBtn = document.getElementById('addAlbumBtn');
    if (addAlbumBtn) {
        addAlbumBtn.addEventListener('click', showAddAlbumModal);
    }
    
    // 添加歌曲按钮
    const addSongBtn = document.getElementById('addSongBtn');
    if (addSongBtn) {
        addSongBtn.addEventListener('click', showAddSongModal);
    }
}

// 显示通知
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg z-50 transition-all duration-300 transform translate-x-full`;
    
    switch (type) {
        case 'success':
            notification.classList.add('bg-green-600', 'text-white');
            break;
        case 'error':
            notification.classList.add('bg-red-600', 'text-white');
            break;
        case 'warning':
            notification.classList.add('bg-yellow-600', 'text-white');
            break;
        default:
            notification.classList.add('bg-blue-600', 'text-white');
    }
    
    notification.textContent = message;
    document.body.appendChild(notification);
    
    // 显示动画
    setTimeout(() => {
        notification.classList.remove('translate-x-full');
    }, 100);
    
    // 自动隐藏
    setTimeout(() => {
        notification.classList.add('translate-x-full');
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}

// 添加系统日志
function addSystemLog(user, userId, action, resource, details, status) {
    const log = {
        id: 'log' + Date.now(),
        timestamp: new Date().toISOString(),
        user: user,
        userId: userId,
        action: action,
        resource: resource,
        details: details,
        ip: '192.168.1.100', // 模拟IP
        status: status
    };
    
    systemLogsData.unshift(log);
    console.log('系统日志:', log);
}

// 设置全局事件监听器
function setupEventListeners() {
    // 退出按钮
    const logoutBtn = document.getElementById('logoutBtn');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', handleLogout);
    }
    
    // 侧边栏切换
    const sidebarToggle = document.getElementById('sidebarToggle');
    const sidebar = document.getElementById('sidebar');
    
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', () => {
            sidebar.classList.toggle('hidden');
        });
    }
}

// 工具函数：格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
}

// 工具函数：格式化数字
function formatNumber(number) {
    return number.toLocaleString();
}

// 工具函数：生成唯一ID
function generateId() {
    return Date.now().toString(36) + Math.random().toString(36).substr(2);
}

// 导出全局函数供HTML使用
window.MusicAdmin = {
    handleLogin,
    handleLogout,
    showNotification,
    addSystemLog,
    formatDate,
    formatNumber,
    generateId
};